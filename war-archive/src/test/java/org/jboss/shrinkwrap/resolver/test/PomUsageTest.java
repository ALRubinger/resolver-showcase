package org.jboss.shrinkwrap.resolver.test;

import java.util.Collection;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.filter.DependencyFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.StrictFilter;
import org.jboss.shrinkwrap.resolver.showcase.test.ArchiveValidationUtil;
import org.junit.Test;

/**
 * This class shows how to load some of the metadata from a pom.xml file. Metadata are for example dependencies and dependency
 * management sections, remote repository definitions are used as well.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class PomUsageTest {
    @Test
    public void getJUnitJarAsArchives() {
        Collection<GenericArchive> junitDeps = DependencyResolvers.use(MavenDependencyResolver.class)
        // load Maven pom file to get information about dependencies, dependency management and remote repositories
                .loadEffectivePom("pom.xml")
                // import only junit dependency from there
                .importAnyDependencies(new DependencyFilter("junit:junit"))
                // StrictFilter causes only the dependencies defined explicitly to be resolved
                .resolveAs(GenericArchive.class, new StrictFilter());

        new ArchiveValidationUtil("junit").validate(junitDeps);
    }

    @Test
    public void getJUnitJarsAsArchives() {
        Collection<GenericArchive> junitDeps = DependencyResolvers.use(MavenDependencyResolver.class)
        // load Maven pom file to get information about dependencies, dependency management and remote repositories
                .loadEffectivePom("pom.xml")
                // import only junit dependency from there
                .importAnyDependencies(new DependencyFilter("junit:junit"))
                // StrictFilter causes only the dependencies defined explicitly to be resolved
                .resolveAs(GenericArchive.class);

        new ArchiveValidationUtil("junit", "hamcrest").validate(junitDeps);
    }

    @Test
    public void getJUnitJarsAsArchivesUp() {
        Collection<GenericArchive> junitDeps = DependencyResolvers.use(MavenDependencyResolver.class)
        // load Maven pom file to get information about dependencies, dependency management and remote repositories
                .loadEffectivePom("pom.xml")
                // return back to the object which allows us to call artifact() method
                .up()
                // version is not required here, it will be determined from a pom file
                .artifact("junit:junit").resolveAs(GenericArchive.class);

        new ArchiveValidationUtil("junit", "hamcrest").validate(junitDeps);
    }

    @Test
    public void getCompileAndRuntimeScopedJarsAsArchives() {
        Collection<GenericArchive> junitDeps = DependencyResolvers.use(MavenDependencyResolver.class)
        // load Maven pom file to get information about dependencies, dependency management and remote repositories
                .loadEffectivePom("pom.xml")
                // import all dependencies in scope compile(default) and runtime
                // empty scope corresponds to no scope defined, that is a compile scope
                .importAnyDependencies(new ScopeFilter("", "runtime", "compile"))
                // get all files defined in the given scopes
                .resolveAs(GenericArchive.class);

        new ArchiveValidationUtil("commons-io").validate(junitDeps);
    }

    @Test
    public void combineCompileAndRuntimeScopedJarsAndJUnitJarAsArchives() {
        Collection<GenericArchive> junitDeps = DependencyResolvers.use(MavenDependencyResolver.class)
        // load Maven pom file to get information about dependencies, dependency management and remote repositories
                .loadEffectivePom("pom.xml")
                // import all dependencies in scope compile(default) and runtime
                // empty scope corresponds to no scope defined, that is actually the compile scope
                .importAnyDependencies(new ScopeFilter("", "runtime", "compile"))
                // return back to parent object
                .up()
                // add junit dependency but exclude hamcrest
                .artifact("junit:junit").exclusion("org.hamcrest:*")
                // get all files defined in the given scopes
                .resolveAs(GenericArchive.class);

        new ArchiveValidationUtil("commons-io", "junit").validate(junitDeps);
    }

}
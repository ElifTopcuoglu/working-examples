import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.api.java.project.JkJavaProject;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

import static dev.jeka.core.api.depmanagement.JkJavaDepScopes.TEST;

class Build extends JkCommands {

    final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    /*
     * Configures plugins to be bound to this command class. When this method is called, option
     * fields have already been injected from command line.
     */
    @Override
    protected void setup() {
        JkJavaProject project = javaPlugin.getProject();
        project.addDependencies(dependencies());
    }

    private JkDependencySet dependencies() {  // Example of dependencies.
        return JkDependencySet.of()
                .and("com.google.guava:guava:21.0")
                .and("junit:junit:4.11", TEST);
    }

    public void cleanPack() {
        clean(); javaPlugin.pack();
    }

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).cleanPack();
    }

}
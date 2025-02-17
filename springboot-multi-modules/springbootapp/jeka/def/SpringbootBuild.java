;import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.system.JkProcess;
import dev.jeka.core.tool.JkClass;
import dev.jeka.core.tool.JkDefClasspath;
import dev.jeka.core.tool.JkDefImport;
import dev.jeka.plugins.springboot.JkPluginSpringboot;
import dev.jeka.plugins.springboot.JkSpringModules.Boot;

import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@JkDefClasspath("dev.jeka:springboot-plugin:3.0.0.RC10")
class SpringbootBuild extends JkClass {

    final JkPluginSpringboot springboot = getPlugin(JkPluginSpringboot.class);

    @JkDefImport("../core")
    private CoreBuild coreBuild;

    @Override
    protected void setup() {
        springboot.setSpringbootVersion("2.5.5");
        springboot.javaPlugin().getProject().simpleFacade()
                .applyOnProject(BuildCommon::setup)
                .setCompileDependencies(deps -> deps
                    .and(Boot.STARTER_WEB)
                    .and(coreBuild.java.getProject().toDependency()))
                .setTestDependencies(deps -> deps
                        .and(Boot.STARTER_TEST))
                .getProject().getConstruction().getCompilation()
                    .getPostCompileActions()
                        .append(this::npmBuild);
    }

    public void cleanPack() {
        clean(); springboot.javaPlugin().pack();
    }

    public void run() {
        this.springboot.run();
    }

    private void npmBuild() {
        JkLog.startTask("Packing web project");
        Path webDir = getBaseDir().resolve("../web");
        Path webDist = webDir.resolve("dist");
        Path staticDir = springboot.javaPlugin().getProject().getConstruction().getCompilation()
                .getLayout().resolveClassDir().resolve("static");
        JkProcess.of("npm", "run", "build").setWorkingDir(webDir).exec();
        JkPathTree.of(webDist).copyTo(staticDir, StandardCopyOption.REPLACE_EXISTING);
        JkLog.endTask();
    }

}
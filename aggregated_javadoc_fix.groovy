// find module-info.java
// get the module name
// then copy the javadoc resources to the javadoc module folder
// in the aggregated javadoc output directory
def dir = "${basedir}"
def javadocOutputDirectory = dir + "/../docs/apidocs"
def directoryName = dir + "/src/main/java"
def fileSubStr = 'module-info.java'
def filePattern = ~/${fileSubStr}/
def directory = new File(directoryName)

if (!directory.isDirectory())
{
    println "The provided directory name ${directoryName} is NOT a directory."
    return
}

println "Searching for files including ${fileSubStr} in directory ${directoryName}..."

def moduleName = null

def findModuleInfoAndGetModuleNameClosure = {
    if (filePattern.matcher(it.name).find()) {
        println "\t${it.name} ${it.path} (size ${it.size()})"
        String fileContent = new File("${it.path}").text

        def matcher = fileContent =~ /module (.*)\{/
        if (matcher.size() == 1) {
            moduleName = matcher[0][1]
            moduleName = moduleName.trim()
            println moduleName
        }
    }
}

def copyNonJavaFilesToAggregatedJavadoc = {
    if (!it.isDirectory() && !it.name.toLowerCase().endsWith(".java")) {
        def path = it.path
        def targetPath = path.replace(directory.getAbsolutePath(), javadocOutputDirectory + "/" + moduleName)
        def target = new File(targetPath)
        if (!target.getParentFile().exists()) {
            target.getParentFile().mkdirs()
        }
        target << it.bytes
        println "Copied " + it.path + " to " + target.path
    }
}

println "Matching Files:"
directory.eachFileRecurse(findModuleInfoAndGetModuleNameClosure)

println "Copying Files:"
directory.eachFileRecurse(copyNonJavaFilesToAggregatedJavadoc)

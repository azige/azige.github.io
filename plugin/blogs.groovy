def s = new StringBuilder()
def fileList = new File("blog").listFiles({path -> path.name.endsWith ".gmd"} as FileFilter)
fileList.sort()
for (def f in fileList){
    def name = f.name.replace(".gmd", "")
    def date = name[0..<10]
    name = name[11..-1]
    def out = new File(f.path.replace(".gmd", ".html"))
    s << "  * <a href=\"${out.path.replaceAll("\\\\", "/")}\">[$date] $name</a>\n"
    if (!gmarkdown.force && out.exists()){
        if (out.lastModified() > f.lastModified()){
            println f.name + " passed."
            continue
        }
        out.delete()
    }
    out.bytes = gmarkdown.process(f.text).getBytes("UTF-8")
    println f.name + ' -> ' + out.name
}
s

def s = new StringBuilder()
def format = new java.text.SimpleDateFormat("yyyy-MM-dd")
for (def f in new File("blog").listFiles({path -> path.name.endsWith ".gmd"} as FileFilter)){
    def name = f.name.replace(".gmd", "")
    def out = new File(f.path.replace(".gmd", ".html"))
    if (out.exists()) out.delete()
    out.bytes = gmarkdown.process(f.text).getBytes("UTF-8")
    s << "  * [[${format.format(f.lastModified())}] $name](${out.path.replaceAll("\\\\", "/")})\n"
}
s
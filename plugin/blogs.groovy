def onStart(def msg){
    msg.addTask 'blog'
}

def s = new StringBuilder()
def fileList = new File("blog").listFiles({path -> path.name.endsWith ".gmd"} as FileFilter)
fileList = fileList.sort{f1, f2 ->
    -(f1.name <=> f2.name)
}

s << '<ul class="nav nav-stacked">\n'
for (def f in fileList){
    def name = f.name.replace(".gmd", "")
    def date = name[0..<10]
    name = name[11..-1]
    def out = new File(f.path.replace(".gmd", ".html"))
    s << "<li><a href=\"${out.path.replaceAll("\\\\", "/")}\">[$date] $name</a></li>\n"
}
s << '</ul>\n'

return s

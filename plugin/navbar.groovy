config = Eval.me new File("navbar-config").text

def items(){
	def s = new StringBuilder()
	for (def pair in config){
		if (mages.currentFile.name.startsWith(pair.value)){
			s << "<li class=\"active\"><a href=\"${strings.siteUrl}/${pair.value}.html\">${pair.key}</a></li>\n"
		}else{
			s << "<li><a href=\"${strings.siteUrl}/${pair.value}.html\">${pair.key}</a></li>\n"
		}
	}
	return s
}

"""
<nav class="navbar navbar-default">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#nav-list">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand">Azige's Home</a>
		</div>
		<div class="collapse navbar-collapse" id="nav-list">
			<ul class="nav navbar-nav">
				${items()}
			</ul>
			<p class="navbar-text navbar-right">
				<a class="navbar-link" href="http://space.bilibili.com/60911"><i class="fa fa-mars-double fa-lg"></i></a>
				<a class="navbar-link" href="http://weibo.com/azige"><i class="fa fa-weibo fa-lg"></i></a>
				<a class="navbar-link" href="https://github.com/azige"><i class="fa fa-github fa-lg"></i></a>
			</p>
		</div>
	</div>
</nav>
"""
<script  type="text/javascript"
    src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js">
</script>
<script type="text/javascript">
$.getJSON("http://en.gravatar.com/azige.json?callback=?", function(data){
	var entry = data.entry[0];
	$("#avatar").attr("src", "http://www.gravatar.com/avatar/" + entry.hash + "?s=80");
	$("#name").text(entry.displayName);
});
</script>

<img id="avatar" alt="avatar">
*<span id="name"></span>'s home page*

---

# Hello world! #

test of a java web start application

<script src="http://java.com/js/deployJava.js"></script>
<script>
    deployJava.createWebStartLaunchButton("launch.jnlp")
</script>
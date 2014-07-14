# 初探 Spring Web MVC —— Simple BBS 的开发 #

学 Web 这么久了，一直都用的是 Pure Java Web ，而大名鼎鼎的 SSH 框架竟然一个都没碰过，想来惭愧，于是打算了解一下，顺便日后还可以在简历里多添一行。  
但是 Java EE 7 的组件中，数据持久化有 JPA ，依赖注入有 CDI ，已经能解决大部分问题，唯一不足就是作为表示层框架的 JSF 用起来实在是太不舒服了。 JSF 封装得太厚，而且约定优先于配置已经优先到根本没法配置了。最终打算尝试一下备受好评的 Spring Web MVC 。

依赖的问题就交给 Maven 解决，现在想来，如果学校的老师在教 SSH 三大框架之前能够先教一下 Maven 的话能够让多少人从三大框架的一系列复杂的依赖和版本的配置问题中解放出来。  
在pom.xml中加入

	<dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>4.0.5.RELEASE</version>
    </dependency>

关于数据层和业务层就略过了，现在重点要研究的是表示层的 Spring Web MVC 框架的使用，其 MVC 模式表现得很明确：用户的请求由 Controller 接收，进行业务操作后将需要的数据放在 Model 里交给 View，并由 View 来渲染返回数据（可以是页面也可能是用于 AJAX 的 JSON 数据等）。

为了把表示层交给 Spring Web MVC 处理，首先要在 web.xml 中配置 Spring 的转发器

    <servlet>
        <servlet-name>spring</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>spring</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

这样所有的 / 目录下的请求，也就是说所有的请求都交给 Spring 转发器处理了，似乎以前流行的是过滤 *.do 或 *.action 这样的请求，不过现在还是让 URL 稍微简洁一点吧。

接下来编写 spring-servlet.xml ，这个配置文件的名字实际上是来自 {servlet-name}-servlet.xml ，也就是用于配置我们刚才所配置的名为"spring"的 Servlet 。

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	       xmlns:mvc="http://www.springframework.org/schema/mvc"
	       xmlns:context="http://www.springframework.org/schema/context"
	       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
	       http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-3.0.xsd
	       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
	">
	    <mvc:resources mapping="/static/**" location="/static/" />
	    <mvc:annotation-driven />
	    <context:component-scan base-package="io.github.azige.bbs.controller" />
	    <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
	        <property name="basename" value="io.github.azige.bbs.web.Message" />
	        <property name="useCodeAsDefaultMessage" value="true" />
	    </bean>
		......
	</beans>

首先， <mvc:resources> 标签配置了静态资源的目录，所有 /static/ 目录下的请求都被认为是静态资源的请求，对于这些请求， Spring 转发器将直接从 web 根目录下读取对应的资源来响应。这可以用来存放诸如 js 脚本、样式表、图片等静态资源。  
其次， <mvc:annotation-driven> 标签配置了使用注解来完成其它配置的功能，这样就可以利用 Java 的注解的特性直接在源代码中标注诸如控制器的声明， URL 到方法、参数的映射等。
接下来 <context:component-scan> 配置了 Spring 要去扫描的包，这样 Spring 可以在运行时自动的去发现组件。
剩下的是一个消息资源包的配置，用于将消息资源注入各种上下文中。

配置好这些之后，就可以进入 Controller 的编写了，用 TopicController （也就是 BBS 的话题相关的 Controller ）来实际看一下。

	@Controller
	public class TopicController{
	
	    @Inject
	    TopicManager topicManager;
	
	    @RequestMapping(value = "/topic", method = RequestMethod.POST)
	    public String topicPublish(
	        @RequestParam(value = "title") String title,
	        @RequestParam(value = "content") String content,
	        HttpServletRequest request,
	        Model model
	    ){
	        Profile loginProfile = (Profile)request.getSession().getAttribute("loginProfile");
	        if (loginProfile == null){
	            model.addAttribute("err", "err.notLoginedYet");
	            return "/WEB-INF/view/error.jsp";
	        }else{
	            topicManager.publishTopic(loginProfile, title, content);
	            return "redirect:/";
	        }
	    }
	
	    @RequestMapping("/topic/{id}")
	    public String topicView(
	        @PathVariable Long id,
	        Model model
	    ){
	        Topic topic = topicManager.getTopicFacade().find(id);
	        if (topic == null){
	            model.addAttribute("error", "err.topicNotExist");
	            return "/WEB-INF/view/error.jsp";
	        }
	        model.addAttribute("topic", topic);
	        return "/WEB-INF/view/topic.jsp";
	    }
	
	    @RequestMapping(value = "/comment", method = RequestMethod.POST)
	    public String commentPublishAjax(
	        @RequestParam("topic_id") Long topicId,
	        @RequestParam("content") String content,
	        HttpServletRequest request,
	        HttpServletResponse response,
	        Model model
	    ) throws IOException{
	        JsonObjectBuilder builder = Json.createObjectBuilder();
	        Profile profile = (Profile)request.getSession().getAttribute("loginProfile");
	        if (profile == null){
	            model.addAttribute("error", "err.notLoginedYet");
	        }else{
	            try{
	                topicManager.publishComment(profile, topicId, content);
	                builder.add("status", "ok");
	                response.setContentType("application/json");
	                try (JsonWriter writer = Json.createWriter(response.getOutputStream())){
	                    writer.writeObject(builder.build());
	                }
	                return null;
	            }catch (BusinessException ex){
	                model.addAttribute("error", ex.getMessage());
	            }
	        }
	
	        return "/WEB-INF/view/error-json.jsp";
	    }
	}

@Controller 标注告诉 Spring 这是一个 Controller 组件。接下来用 @Inject 标注了一个 TopicManager 对象（这是一个处理业务的 EJB ）的注入， @Inject 来自 JSR 299 ，也就是 Java EE CDI ，现在它也受到 Spring 的支持。  
每个方法前都带有 @RequestMapping 标注，这个标注可以将 URL 映射到方法，也就是说，在请求这些 URL 的时候， String 转发器会将请求变成对这些方法的调用，此外也可以限定 HTTP 请求的方法。在参数上加上 @RequestParam 或 @PathVariable 标注可以将请求或 URL 中的参数映射到这些方法参数上， Spring 转发器可以直接注入这些参数而不需要我们自己再做额外的提取工作。除去参数以外，一些常用的 HTTP 对象或者是 Model 也能够注入。  
Controller 的方法所需要做的事就是调用 EJB 处理业务，然后决定如何渲染视图，方法返回类型可以是 String ，这种情况 Spring 转发器将使用返回的字符串作为路径来寻找可以渲染视图的资源（通常是 jsp ）。另外也可以是 Spring 中定义的类型 ModelAndView ，这是一个将 Model 和 View 绑定在一起的类型，不过本人目前尚未使用过，要详细了解可以查看 Spring 的 API 文档。

利用 Spring Web MVC 框架可以很轻易的在 web 应用的表示层中实现 MVC 模式，并且与 Java Web 的其它组件的相容性也很好（例如很容易与 JAX-RS 一起使用，如果有机会的话下次可以研究一下这个话题）。不过本人也没用过 Struts ，所以也没法与其比较优劣，但既然 Spring Framework 有取代整个 SSH 框架的野心的话，相信应该还是有很大潜力的。与 JSF 相比可能需要稍多一点的配置，但这样也让 Spring Web MVC 更灵活，估计能拯救不少强迫症患者吧。

完整的代码可以从仓库里获得  
[https://github.com/azige/simple-bbs](https://github.com/azige/simple-bbs)
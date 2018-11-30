<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
	
	<meta charset="UTF-8"> <!-- for HTML5 -->
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
	<script src='https://cdn.bootcss.com/jquery.form/4.2.2/jquery.form.min.js'></script>
</head>
<body>
<h2>Hello World!</h2>
	          文件:<input type="file" id="myFile" name="file"><br/>
    	<input type="button" value="上传" onclick="upload()" >
	<script type="text/javascript"  charset="UTF-8">
		
		function upload(){
			var fileObj = document.getElementById("myFile").files[0]; // js 获取文件对象
            if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
                alert("请选择图片");
                return;
            }
            var formFile = new FormData();
            formFile.append("action", "/file/upload2");
            formFile.append("account", "18301236738");
            formFile.append("file", fileObj); //加入文件对象
            $.ajax({
                url: "/file/upload2",
                data: formFile,
                type: "POST",
                dataType: "json",
                cache: false,//上传文件无需缓存
                processData: false,//用于对data参数进行序列化处理 这里必须false
                contentType: false, //必须
                success: function (result) {
                    alert(result.result+"---"+result.msg);
                },
            });
		}	
		
		function test2(){
			//这里使用的是jquery.form.js的功能
			$("#tf").ajaxSubmit(function(msg) { 
				var json=JSON.parse(msg);//js自带的json
				//alert(json.msg);
				 $("#result").val(json.msg);
				 $("#tu1").attr("src",json.src_1);
				 $("#tu2").attr("src",json.src_2);
			}); 
			return false;
		}
    </script>
</body>
</html>

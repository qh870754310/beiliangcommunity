/**
 * Created by zhengda.li on 2017/7/23.
 */
var UP_IMGCOUNT = 0;//上传图片张数记录
$(function () {
    $("#imgSelect").click(function () {
        var _CRE_FILE = document.createElement("input");
        if ($(".imgFile").length <= $(".imgLook").length) {
            _CRE_FILE.setAttribute("type", "file");
            _CRE_FILE.setAttribute("class", "imgFile");
            _CRE_FILE.setAttribute("accept", "image/*");
            _CRE_FILE.setAttribute("num", UP_IMGCOUNT);//记录此对象对应的编号
            $("#imgSelect").after(_CRE_FILE);
            $(".imgFile").off("change").on("change", function () {
                if ($(this).val().length > 0) {//判断是否有选中图片
                    var file = this.files[0];//获取file文件对象
                    //创建预览外层
                    var _prevdiv = document.createElement("div");
                    _prevdiv.setAttribute("class", "imgLook");
                    //创建内层img对象
                    var preview = document.createElement("img");
                    $(_prevdiv).append(preview);
                    //创建删除按钮
                    var IMG_DELBTN = document.createElement("div");
                    IMG_DELBTN.setAttribute("class", "imgDel");
                    IMG_DELBTN.innerHTML = "移除";
                    $(_prevdiv).append(IMG_DELBTN);
                    //记录此对象对应编号
                    _prevdiv.setAttribute("num", $(this).attr("num"));
                    //对象注入界面
                    $("#imgSelect").before(_prevdiv);
                    UP_IMGCOUNT++;//编号增长防重复
                    //预览功能
                    var reader = new FileReader();//创建读取对象
                    reader.onloadend = function () {
                        preview.src = reader.result;//读取加载，将图片编码绑定到元素
                    };
                    if (file) {//如果对象正确
                        reader.readAsDataURL(file);//获取图片编码
                    } else {
                        preview.src = "";//返回空值
                    }
                    $('.imgLook').off("mouseover").on("mouseover", function () {
                        $(this).find('.imgDel').show();
                    }).off("mouseout").on("mouseout", function () {
                        $(this).find('.imgDel').hide();
                    });
                    $('.imgDel').off("click").on("click", function () {
                        $(".imgFile[num=" + $(this).parent().attr("num") + "]").remove();//移除图片file
                        $(this).parent().remove();//移除图片显示
                    });
                }
            });
        } else {
            _CRE_FILE = $(".imgFile").eq(0).get(0);
        }
        return $(_CRE_FILE).click();//打开对象选择框
    });
});
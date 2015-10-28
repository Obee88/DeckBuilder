
function showImage(src, component){

    var imageView = document.getElementById("imageView");
    var left = parseInt(component.offsetLeft)+component.offsetWidth;
    var top = parseInt(component.offsetTop)- 225;
    imageView.src = src;
    imageView.style.display = "";
    imageView.style.position = "absolute";
    var componentLeftOffset = parseInt(component.offsetLeft);
    var componentTopOffset  = parseInt(component.offsetTop);
    var imageWindowHeight = 445, imageWindowWidth = 312;
    var scrollTop = $("#bd").scrollTop();
    var documentWidth = document.getElementById("bd").offsetWidth;
    var documentHeight = document.getElementById("bd").offsetHeight; //+document.getElementById("bd").offsetHeight+document.getElementById("ft").offsetHeight;
    var minTop = scrollTop;
    var maxTop = scrollTop + documentHeight - imageWindowHeight;
    var maxLeft = documentWidth - imageView.offsetWidth;
    if (top>maxTop) top = maxTop;
    if (top< minTop) top = minTop;
    if (left>maxLeft) left = maxLeft;

    imageView.style.left = left+"px";
    imageView.style.top= top+"px";
}
function hideImage(){
    var imageView = document.getElementById("imageView");
    imageView.style.display = "none";
}
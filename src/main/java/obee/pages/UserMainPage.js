
function showImage(src, component){

    var imageView = document.getElementById("imageView");
    var left = parseInt(component.offsetLeft)+component.offsetWidth;
    var top = parseInt(component.offsetTop)- 225;
    if(top<0) top=0;
    imageView.src = src;
    imageView.style.display = "";
    imageView.style.position = "absolute";
    imageView.style.left = left+"px";
    imageView.style.top= top+"px";
    debugger;
}
function hideImage(){
    var imageView = document.getElementById("imageView");
    imageView.style.display = "none";
}
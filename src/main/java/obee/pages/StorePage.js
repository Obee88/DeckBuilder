$( document ).ready(function() {
    resetFields();
    balance = parseInt($("#jadBalanceLabel")[0].innerHTML);
    prices = {
        "common" : parseInt($("#commonPriceLbl")[0].innerHTML),
        "uncommon" : parseInt($("#uncommonPriceLbl")[0].innerHTML),
        "rare" : parseInt($("#rarePriceLbl")[0].innerHTML),
        "mythic" : parseInt($("#mythicPriceLbl")[0].innerHTML)
    };
});
function btnClicked(sender, change, rarity){
    change =parseInt(change);
    var input = $("#"+rarity+"Cnt")[0];
    var label = $("#"+rarity+"CntLbl")[0];
    var inputValue = parseInt(input.value);
    if ( inputValue + change<0) return;
    var price = prices[rarity];
    var total = parseInt($("#totalPrice")[0].innerHTML);
    if (total+(price*change)>balance) {
        alert("Not enough Jad! Come back when you get some more Jads!");
        return;
    }
    input.value = inputValue + change;
    label.innerHTML = input.value;
    $("#totalPrice")[0].innerHTML = total + (price * change);
    return false;
}
function submitForm(){
    var form = $("form#buyForm")[0];
    form.submit();
}
function resetFields(){
    debugger;
    $("#commonCnt")[0].value = "0";
    $("#uncommonCnt")[0].value = "0";
    $("#rareCnt")[0].value = "0";
    $("#mythicCnt")[0].value = "0";
}
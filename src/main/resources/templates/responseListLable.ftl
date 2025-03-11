{
"content": [
<#list labels as label>
    {
    "lable_id": "${label.id}",
    "lable_name": "${label.labelName}"
    }<#sep>,</#sep>
</#list>
],
"pageNo": ${pageNo},
"pageSize": ${pageSize},
"total": ${total},
"totalPages": ${totalPages}
}

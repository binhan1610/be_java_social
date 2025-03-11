{
"note_id":"${note.id}",
"note_topic":"${note.topic}",
"note_important": ${note.important?string('true', 'false')},
"note_success": ${note.success?string('true', 'false')},
"list_image": [
<#list note.images as image>
    {
    "image_id": "${image.id}",
    "image_link": "${image.image_link}"
    }<#sep>,</#sep>
</#list>
],
"list_title": [
<#list note.titles as title>
    {
    "title_id": "${title.id}",
    "title_link": "${title.title}"
    }<#sep>,</#sep>
</#list>
],
<#if note.lable??>
"lable":
    {
    "lable_id": "${note.lable.id!}",
    "lable_name": "${note.lable.labelName!}"
    }
    <#else>
    "lable":{}
</#if>

}
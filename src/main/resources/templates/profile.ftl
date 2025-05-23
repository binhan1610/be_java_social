{
"user_id":"${profileId?c}",
<#assign first = true>
<#if email?has_content>
    <#if !first>,</#if>"email": "${email}"<#assign first = false>
</#if>
<#if phoneNumber?has_content>
    <#if !first>,</#if>"phoneNumber": "${phoneNumber}"<#assign first = false>
</#if>
<#if firstName?has_content>
    <#if !first>,</#if>"firstName": "${firstName}"<#assign first = false>
</#if>
<#if lastName?has_content>
    <#if !first>,</#if>"lastName": "${lastName}"<#assign first = false>
</#if>
<#if avatar?has_content>
    <#if !first>,</#if>"avatar": "${avatar}"<#assign first = false>
</#if>
<#if birthDay?has_content>
    <#if !first>,</#if>"birthDay": "${birthDay}"<#assign first = false>
</#if>
<#if address?has_content>
    <#if !first>,</#if>"address": "${address}"<#assign first = false>
</#if>
<#if sex?has_content>
    <#if !first>,</#if>"sex": "${sex}"<#assign first = false>
</#if>
}

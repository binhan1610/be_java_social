[
<#list friends as friend>
    {
    "avatar_image": "${friend.image}",
    "username": "${friend.username}",
    "user_id": "${friend.userid}"
    }<#if friend_has_next>,</#if>
</#list>
]

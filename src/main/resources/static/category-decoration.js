//     var a = document.getElementById("postCategory");
//     var text = a.innerText;
//     console.log(text);
//     switch (text) {
//     case "World": a.classList.add("text-primary"); break;
//     case "U.S.": a.classList.add("text-secondary");break;
//     case "Technology":a.classList.add("text-success"); break;
//     case "Design":a.classList.add("text-danger"); break;
//     case "Business": a.classList.add("text-warning");break;
//     case "Culture":a.classList.add("text-info"); break;
//     case "Politics":a.classList.add("text-dark"); break;
//     case "Opinion":a.classList.add("text-body"); break;
//     case "Science":a.classList.add("text-muted"); break;
//     case "Health":a.classList.add("text-primary");break;
//     case "Style": a.classList.add("text-secondary");break;
//     case "Travel":a.classList.add("text-success"); break;
// }
    /*<![CDATA[*/

    /*[# th:each="post: ${posts}"]*/
    var title =  /*[[${post.title}]]*/ 'default';
    console.log(title);
    /*[/]*/

    /*]]>*/
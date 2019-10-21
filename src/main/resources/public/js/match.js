window.onload = function() {

const matchId = document.head.querySelector("[name=matchId][content]").content;

fetch("/replayGame/"+matchId)
    .then(function(response){
    if (response.status !== 200) {
            console.log('Looks like there was a problem. Status Code: ' +
              response.status);
            return;
          }
     response.json().then(function(data) {
            console.log(data);
            document.head.querySelector("[name=replayMatch][content]").content = JSON.stringify(data)
          });
    })
    .catch(function(err) {
        console.log('Fetch Error :-S', err);
      });

const canvas = document.getElementById("game-canvas");
const context = canvas.getContext("2d");

document.getElementById("draw-game-canvas").onclick = function fun(){
 context.clearRect(0, 0, canvas.width, canvas.height);

 const positions = JSON.parse(document.head.querySelector("[name=replayMatch][content]").content)

 Object.keys(positions).forEach(function(name){
 const points = positions[name];
 draw(context, points);
 });

}

function draw(context, points){
context.beginPath();
 for (i = 0; i < points.length-1; i++) {

   var point = points[i]
   var next = points[i+1]
   console.log(point + " : "+next )

   context.moveTo(point[0] * 20,point[1]*20);
   context.lineTo(next[0]*20,next[1]*20);

  }
 context.stroke();}
}
window.onload = function() {

var canvas = document.getElementById("game-canvas");
var context = canvas.getContext("2d");

var points = [[0,0],[180,47], [280,47]];

document.getElementById("draw-game-canvas").onclick = function fun(){
  context.clearRect(0, 0, canvas.width, canvas.height);

 context.beginPath();
 for (i = 0; i < 2; i++) {

   var point = points[i]
   var next = points[i+1]
   console.log(point + " : "+next )

   context.moveTo(point[0],point[1]);
   context.lineTo(next[0],next[1]);

  }
 context.stroke();

}
}
var fs = require('fs');

function iterate(fn, object) {
    if (!(object instanceof Array)) {
        for (var item in object) {
            if (object[item] === "red") {
                return;
            }
        }
    }
    
    for (var item in object) {
        if (object[item] !== null && (
            typeof(object[item]) == "object" ||
            Array.isArray(object[item])
        )) {
            iterate(fn, object[item]);
        } else {
            fn.call(this, item, object[item]);  
        }
    }
}

fs.readFile( __dirname + '/input', function (err, data) {
  if (err) {
    throw err; 
  }

  var parsed = JSON.parse(data.toString());

  var sum = 0;

  iterate(function(key, val) {
    if (!isNaN(val)) {
        sum += val;
    }
  }, parsed);

  console.log(sum);
});

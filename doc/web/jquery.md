

```javascript
// checkbox
var countChecked = function() {
  var n = $( "input:checked" ).length;
  $( "div" ).text( n + (n === 1 ? " is" : " are") + " checked!" );
};
countChecked();
 
$( "input[type=checkbox]" ).on( "click", countChecked );
</script>




// radio
<form>
  <div>
    <input type="radio" name="fruit" value="orange" id="orange">
    <label for="orange">orange</label>
  </div>
  <div>
    <input type="radio" name="fruit" value="apple" id="apple">
    <label for="apple">apple</label>
  </div>
  <div>
    <input type="radio" name="fruit" value="banana" id="banana">
    <label for="banana">banana</label>
  </div>
  <div id="log"></div>
</form>
 
<script>
$( "input" ).on( "click", function() {
  $( "#log" ).html( $( "input:checked" ).val() + " is checked!" );
});
</script>
```
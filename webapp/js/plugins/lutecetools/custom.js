/**
 * 
 */
var dataGithub = [
{
	value: $("#valGithubRed").val(),
	color:"red",
	highlight: "#FF5A5E",
	label: "Github DANGER"
},
{
	value: $("#valGithubGreen").val(),
	color: "#00b300",
	highlight: "#47d147",
	label: "Github OK"
},
{
	value: $("#valGithubOrange").val(),
	color: "orange",
	highlight: "#FFC870",
	label: "Github WARNING"
}
];
	
var dataJira = [
{
	value: $("#valJiraRed").val(),
	color:"red",
	highlight: "#FF5A5E",
	label: "Jira DANGER"
},
{
	value: $("#valJiraGreen").val(),
	color: "#00b300",
	highlight: "#47d147",
	label: "Jira OK"
},
{
	value: $("#valJiraOrange").val(),
	color: "orange",
	highlight: "#FFC870",
	label: "Jira WARNING"
}
];

var dataReadme = [
{
	value: $("#valReadmeRed").val(),
	color:"red",
	highlight: "#FF5A5E",
	label: "Readme DANGER"
},
{
	value: $("#valReadmeGreen").val(),
	color: "#00b300",
	highlight: "#47d147",
	label: "Readme OK"
}
];

var dataRCI = [
{
	value: $("#valRCIRed").val(),
	color:"red",
	highlight: "#FF5A5E",
	label: "RCI DANGER"
},
{
	value: $("#valRCIGreen").val(),
	color: "#00b300",
	highlight: "#47d147",
	label: "RCI OK"
},
{
	value: $("#valRCIOrange").val(),
	color: "orange",
	highlight: "#FFC870",
	label: "RCI WARNING"
}
];

// Charts in components page

var ctx1 = $("#pieChart1").get(0).getContext("2d");
var myDoughnutChart = new Chart(ctx1).Doughnut(dataGithub);

var ctx2 = $("#pieChart2").get(0).getContext("2d");
var myDoughnutChart = new Chart(ctx2).Doughnut(dataJira)
	
var ctx3 = $("#pieChart3").get(0).getContext("2d");
var myDoughnutChart = new Chart(ctx3).Doughnut(dataReadme);
	
var ctx4 = $("#pieChart4").get(0).getContext("2d");
var myDoughnutChart = new Chart(ctx4).Doughnut(dataRCI);


// Autocompletion graphics

function handleHeaderClick( hdr ) {
	return function( ) {
	    var concat = hdr.concat(' i');
	    
	    if ($( hdr ).hasClass( 'sortUp' ) == true) {
	        $( hdr ).removeClass( 'sortUp' );
	        $( hdr ).addClass( 'sortDown' );
	        $( concat ).removeClass( 'fa-sort fa-sort-asc' ).addClass( 'fa-sort-desc' );
	    } else if ($( hdr ).hasClass( 'sortDown' ) == true) {
	        $( hdr ).removeClass( 'sortDown' );
	        $( hdr ).addClass( 'sortUp' );
	        $( concat ).removeClass( 'fa-sort fa-sort-desc' ).addClass( 'fa-sort-asc' );
	    } else {
	    	$( hdr ).removeClass( 'sortUp sortDown' );
	        $( hdr ).addClass( 'sortUp' );
	        $( concat ).removeClass( 'fa-sort' ).addClass( 'fa-sort-asc' );
	    }
	}
}

var id;
for ( id = 1; id <= 22; id++ ) {
	$( ".myTableGraphic #" + id ).append( " <i class=\"fa fa-fw fa-sort\">" );
}

//$( ".myTableGraphic" ).tablesorter( {debug: true} );
$( "" ).toggle( );

$( ".myTableGraphic th" ).css( "cursor", "pointer" );

for ( id = 1; id <= 22; id++ ) {
	$( "#" + id ).click( handleHeaderClick( "#" + id ) );
}

// Filter components list

var timer;
var timeout = 500;
var list = $( ".component-box" );
var img = $( '#imgLoader' );
var input = $( "#component" );
input.keyup( function ( ) {
	clearTimeout( timer );
	img.show( );
	timer = setTimeout( function( ) {
		var search = input.val( );
		for ( var i = 0; i < list.length; i++ ) {
			var el = list[i];
			var pluginName = $( el ).attr( "data-component" );
			if ( pluginName.match( search ) == null ) {
				$( el ).hide( );
			}
			else {
				$( el ).show( );
			}
		}
		img.hide( );
	}, timeout );
} );

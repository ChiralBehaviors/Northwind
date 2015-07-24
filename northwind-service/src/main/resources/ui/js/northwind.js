var northwindWspUri = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/v1";
var scenarioWspUri = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/scenario/v1";

var northwind = angular.module('northwind', [ 'ngRoute',
		'northwindControllers', 'phantasm-at-rest' ]);

northwind.config([ '$routeProvider', "WorkspaceLookup", function($routeProvider, WorkspaceLookup) {
	var northwindWsp = WorkspaceLookup.one(northwindWspUri);
	var scenarioWsp = WorkspaceLookup.one(northwindWspUri);
	$routeProvider.when('/json-ld/facet', {
		templateUrl : 'partials/facet-ruleforms.html',
		controller : 'FacetRuleformsListCtrl'
	}).when('/json-ld/facet/:ruleform', {
		templateUrl : 'partials/facets.html',
		controller : 'FacetListCtrl'
	}).when('/json-ld/facet/:ruleform/:classifier/:classification', {
		templateUrl : 'partials/facet-instances.html',
		controller : 'FacetInstancesListCtrl'
	}).when('/json-ld/facet/:ruleform/:classifier/:classification/:instance', {
		templateUrl : 'partials/facet-instance-detail.html',
		controller : 'FacetInstanceDetailCtrl'
	});
	$routeProvider.otherwise({
		redirectTo : '/json-ld/facet'
	});
} ]);
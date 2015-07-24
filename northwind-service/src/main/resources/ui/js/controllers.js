var northwindControllers = angular.module('northwindControllers', []);

function isObject(obj) {
	return obj === Object(obj);
}

var r = new RegExp('^(?:[a-z]+:)?//', 'i');

function relativize(data) {
	for ( var key in data) {
		var prop = data[key];
		if (isObject(prop)) {
			relativize(prop);
		} else if (r.test(prop)) {
			var parser = document.createElement('a');
			parser.href = prop;
			data[key] = parser.pathname;
		}
	}
}

northwindControllers.controller('FacetInstancesListCtrl', [
		'$scope',
		'$http',
		'$routeParams',
		function($scope, $http, $routeParams) {
			$http.get(
					'/json-ld/facet/' + $routeParams.ruleform + '/'
							+ $routeParams.classifier + '/'
							+ $routeParams.classification).success(
					function(data) {
						for ( var idx in data) {
							relativize(data[idx]);
						}
						$scope.facetInstances = data;
					});
		} ]);

northwindControllers.controller('FacetListCtrl', [
		'$scope',
		'$http',
		'$routeParams',
		function($scope, $http, $routeParams) {
			$http.get('/json-ld/facet/' + $routeParams.ruleform).success(
					function(data) {
						$scope.facets = data['@graph'];
					});
		} ]);

northwindControllers.controller('FacetRuleformsListCtrl', [ '$scope', '$http',
		function($scope, $http) {
			$http.get('/json-ld/facet').success(function(data) {
				relativize(data);
				$scope.facetRuleforms = data;
			});
		} ]);

northwindControllers.controller('FacetDetailCtrl', [
		'$scope',
		'$http',
		'$routeParams',
		function($scope, $http, $routeParams) {
			$http.get(
					'/json-ld/facet/' + $routeParams.ruleform + '/'
							+ $routeParams.classifier + '/'
							+ $routeParams.classification).success(
					function(data) {
						$scope.facet = data;
					});
		} ]);

northwindControllers.controller('FacetInstanceDetailCtrl', [
		'$scope',
		'$http',
		'$routeParams',
		function($scope, $http, $routeParams) {
			$http.get(
					'/json-ld/facet/' + $routeParams.ruleform + '/'
							+ $routeParams.classifier + '/'
							+ $routeParams.classification + '/'
							+ $routeParams.instance).success(function(data) {
				relativize(data);
				$scope.facetInstance = data;
			});
		} ]);
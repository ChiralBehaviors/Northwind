(function() {
	var northwindWspUri = window
			.encodeURIComponent("uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/v1");
	var northwindWsp = 'json-ld/workspace-mediated/' + northwindWspUri + "/";
	var customers = northwindWsp + 'facet/Agency/kernel|IsA/Customer/';

	var northwind = angular.module('northwind', [ 'ngRoute',
			'northwindControllers', 'restangular' ]);

	northwind.config([ '$routeProvider', 'RestangularProvider',
			function($routeProvider, RestangularProvider) {
				RestangularProvider.setBaseUrl("/");
				$routeProvider.when("/customer/:instance", {
					templateUrl : 'partials/customer-detail.html',
					controller : 'CustomerDetailControl'
				});
				$routeProvider.when("/customer", {
					templateUrl : 'partials/customers.html',
					controller : 'CustomersControl'
				});
				$routeProvider.otherwise({
					redirectTo : "/customer"
				});
			} ]);

	northwind.factory("Northwind", [ "Restangular", function(Restangular) {
		var service = Restangular.service(northwindWsp);
		return service;
	} ]);

	northwind.factory("Customers", [ "Restangular", function(Restangular) {
		var service = Restangular.service(customers);
		return service;
	} ]);
})();
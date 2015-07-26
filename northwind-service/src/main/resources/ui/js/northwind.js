(function() {

	var northwind = angular.module('northwind', [ 'ngRoute',
			'northwindControllers', 'phantasm' ]);

	northwind.config([ '$routeProvider', function($routeProvider) {
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

	northwind
			.service(
					"Customers",
					[
							"WorkspacePhantasm",
							function(WorkspacePhantasm) {
								var northwindUri = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/v1";
								this.instances = function() {
									return WorkspacePhantasm.facetInstances(
											northwindUri, "Agency",
											"kernel|IsA", "Customer");
								};
								this.instance = function(ref) {
									return WorkspacePhantasm.facetInstance(
											northwindUri, "Agency",
											"kernel|IsA", "Customer", ref);
								};
							} ]);
})();
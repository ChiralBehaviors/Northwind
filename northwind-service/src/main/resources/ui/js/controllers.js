var northwindControllers = angular.module('northwindControllers',
		[ 'jsonFormatter' ]);

northwindControllers.controller('CustomersControl', [
		'$scope',
		'Customers',
		'PhantasmRelative',
		function($scope, Customers, PhantasmRelative) {
			Customers.instances().get().then(
					function(data) {
						var instances = data.instances;
						for ( var key in instances) {
							instances[key]["@id"] = PhantasmRelative
									.instance(instances[key]["@id"]);
						}
						$scope.customers = instances;
					});
		} ]);

northwindControllers.controller('CustomerDetailControl', [
		'$scope',
		'$routeParams',
		'Customers',
		function($scope, $routeParams, Customers) {
			Customers.instance($routeParams.instance).get().then(
					function(customer) {
						$scope.customer = customer.plain();
					});
		} ]);
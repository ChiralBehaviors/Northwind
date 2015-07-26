var northwindControllers = angular.module('northwindControllers',
		[ 'jsonFormatter' ]);

northwindControllers.controller('CustomersControl', [ '$scope', 'Customers',
		function($scope, Customers) {
			Customers.instances().get().then(function(data) {
				for ( var key in data.instances) {
					var id = data.instances[key]["@id"];
					id = id.substr(id.lastIndexOf('/') + 1);
					data.instances[key]["@id"] = id;
				}
				$scope.customers = data.instances;
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
var northwindControllers = angular.module('northwindControllers',
		[ 'jsonFormatter' ]);

var r = new RegExp('^(?:[a-z]+:)?//', 'i');

function relativize(data) {
	for ( var key in data) {
		var prop = data[key];
		if (angular.isObject(prop)) {
			relativize(prop);
		} else if (r.test(prop)) {
			var parser = document.createElement('a');
			parser.href = prop;
			data[key] = parser.pathname;
		}
	}
}

northwindControllers.controller('DefaultCtrl', [ "$scope", function($scope) {
} ]);

northwindControllers.controller('CustomersControl', [ '$scope', 'Customers',
		function($scope, Customers) {
			Customers.one().get().then(function(data) {
				var customers = data['@graph'];
				for ( var key in customers) {
					var customer = customers[key];
					customer['@id'] = "/customer/" + customer['@id'];
				}
				$scope.customers = customers;
			});
		} ]);

northwindControllers.controller('CustomerDetailControl', [ '$scope',
		'$routeParams', 'Customers', function($scope, $routeParams, Customers) {
			Customers.one($routeParams.instance).get().then(function(customer) {
				relativize(customer);
				$scope.customer = customer;
			})
		} ]);
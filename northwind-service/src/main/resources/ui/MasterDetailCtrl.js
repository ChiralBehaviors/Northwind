var myApp = angular.module('myApp', [ "phantasm" ]);

var northwindUri = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/v1";

// Force AngularJS to call our JSON Web Service with a 'GET' rather than an
// 'OPTION'
// Taken from: http://better-inter.net/enabling-cors-in-angular-js/
myApp.config([ '$httpProvider', function($httpProvider) {
	$httpProvider.defaults.useXDomain = true;
	delete $httpProvider.defaults.headers.common['X-Requested-With'];
} ]);

myApp.service("Customers", [
		'WorkspacePhantasm',
		'PhantasmRelative',
		function(WorkspacePhantasm, PhantasmRelative) {
			this.instances = function() {
				return WorkspacePhantasm.facetInstances(northwindUri, "Agency",
						"kernel|IsA", "Customer");
			};

			this.instance = function(customer) {
				var instance = PhantasmRelative.instance(customer);
				return WorkspacePhantasm.facetInstance(northwindUri, "Agency",
						"kernel|IsA", "Customer", instance);
			};

			var selection = [ "orders", "itemDetails" ];
			this.ordersOf = function(customer) {
				var instance = PhantasmRelative.instance(customer);
				return WorkspacePhantasm.select(northwindUri, "Agency",
						"kernel|IsA", "Customer", instance, selection);
			};
		} ]);

myApp.filter('sumByKey', function() {
	return function(data, key) {
		if (typeof (data) === 'undefined' || typeof (key) === 'undefined') {
			return 0;
		}
		var sum = 0;
		for (var i = data.length - 1; i >= 0; i--) {
			sum += parseInt(data[i][key]);
		}
		return sum;
	};
});

myApp.filter('customSum', function() {
	return function(listOfProducts, key) {
		// Count how many items are in this order
		var total = 0;
		angular.forEach(listOfProducts, function(product) {
			// alert(product + "." + key);
			total += eval("product." + key);
		});
		return total;
	};
});

myApp.filter('countItemsInOrder', function() {
	return function(listOfProducts) {
		// Count how many items are in this order
		var total = 0;
		angular.forEach(listOfProducts, function(product) {
			total += product.Quantity;
		});
		return total;
	};
});

myApp.filter('orderTotal', function() {
	return function(listOfProducts) {
		// Calculate the total value of a particular Order
		var total = 0;
		angular.forEach(listOfProducts, function(product) {
			total += product.Quantity * product.UnitPrice;
		});
		return total;
	};
});

myApp.controller('MasterDetailCtrl', [
		'$scope',
		'Customers',
		function($scope, Customers) {
			$scope.listOfCustomers = null;
			$scope.selectedCustomer = null;

			Customers.instances().get().then(function(data) {
				$scope.listOfCustomers = data.instances;

				if ($scope.listOfCustomers.length > 0) {
					$scope.selectedCustomer = $scope.listOfCustomers[0]["@id"];
					$scope.loadOrders();
				}
			});

			$scope.selectCustomer = function(val) {
				$scope.selectedCustomer = val["@id"];
				$scope.loadOrders();
			};

			$scope.loadOrders = function() {
				$scope.listOfOrders = null;

				Customers.ordersOf($scope.selectedCustomer).get().then(
						function(data) {
							$scope.listOfOrders = data;
						});
			};
		} ]);

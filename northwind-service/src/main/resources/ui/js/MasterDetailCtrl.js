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
		function(WorkspacePhantasm) {
			this.instances = function() {
				return WorkspacePhantasm.facetInstances(northwindUri, "Agency",
						"kernel|IsA", "Customer");
			};

			this.instance = function(customer) {
				return WorkspacePhantasm.facetInstance(northwindUri, "Agency",
						"kernel|IsA", "Customer", customer);
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
	return function(listOfItems) {
		// Count how many items are in this order
		var total = 0;
		angular.forEach(listOfItems, function(item) {
			total += item.quantity;
		});
		return total;
	};
});

myApp.filter('orderTotal', function() {
	return function(listOfItems) {
		// Calculate the total value of a particular Order
		var total = 0;
		angular.forEach(listOfItems, function(item) {
			total += item.quantity * item["unit price"];
		});
		return total;
	};
});

myApp
		.controller(
				'MasterDetailCtrl',
				[
						'$scope',
						'Customers',
						function($scope, Customers) {
							$scope.listOfCustomers = null;
							$scope.selectedCustomer = null;

							var selection = [ ";a=Customer Name" ];
							Customers
									.instances()
									.get({
										select : selection
									})
									.then(
											function(data) {
												$scope.listOfCustomers = data['@graph'];

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

								var ordersAttrs = ";a=name;a=Required Date;a=Order Date;a=Ship Date";
								var itemDetailsAttrs = ";a=unit price;a=quantity;a=discount;a=tax rate/product/name";
								var selection = [ "orders" + ordersAttrs
										+ "/itemDetails" + itemDetailsAttrs ];
								Customers.instance($scope.selectedCustomer)
										.get({
											select : selection
										}).then(function(data) {
											$scope.listOfOrders = data.orders;
										});
							};
						} ]);

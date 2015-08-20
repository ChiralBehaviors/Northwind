var myApp = angular.module('myApp', ["restangular"]);

var northwindUri = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/v1";

// Force AngularJS to call our JSON Web Service with a 'GET' rather than an
// 'OPTION'
// Taken from: http://better-inter.net/enabling-cors-in-angular-js/
myApp.config(['$httpProvider', 'RestangularProvider', function ($httpProvider, RestangularProvider) {
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
    RestangularProvider.setDefaultHeaders({'Content-Type': 'application/json; charset=UTF-8'});
}]);

myApp.filter('sumByKey', function () {
    return function (data, key) {
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

myApp.filter('customSum', function () {
    return function (listOfProducts, key) {
        // Count how many items are in this order
        var total = 0;
        angular.forEach(listOfProducts, function (product) {
            // alert(product + "." + key);
            total += eval("product." + key);
        });
        return total;
    };
});

myApp.filter('countItemsInOrder', function () {
    return function (listOfItems) {
        // Count how many items are in this order
        var total = 0;
        angular.forEach(listOfItems, function (item) {
            total += item.quantity;
        });
        return total;
    };
});

myApp.filter('orderTotal', function () {
    return function (listOfItems) {
        // Calculate the total value of a particular Order
        var total = 0;
        angular.forEach(listOfItems, function (item) {
            total += item.quantity * item.unitPrice;
        });
        return total;
    };
});

myApp
    .controller(
    'MasterDetailCtrl',
    [
        '$scope',
        'Restangular',
        function ($scope, Restangular) {
            var Northwind = Restangular.one('/graphql').one('workspace').all(encodeURIComponent(northwindUri));
            var ordersQuery = '{ Customer( id: "$id") { orders {id name requiredDate orderDate shipDate itemDetails { id name unitPrice quantity product { id name } } } } }';
            var instancesQuery = '{ InstancesOfCustomer { customerName id description } }';

            $scope.listOfCustomers = null;
            $scope.selectedCustomer = null;

            var request = {query: instancesQuery};
            Northwind
                .post(request)
                .then(
                function (data) {
                    $scope.listOfCustomers = data.InstancesOfCustomer;

                    if ($scope.listOfCustomers.length > 0) {
                        $scope.selectedCustomer = $scope.listOfCustomers[0].id;
                        $scope.loadOrders();
                    }
                });

            $scope.selectCustomer = function (val) {
                $scope.selectedCustomer = val.id;
                $scope.loadOrders();
            };

            $scope.loadOrders = function () {
                $scope.listOfOrders = null;
                var request = {query: ordersQuery.replace('$id', $scope.selectedCustomer)};
                Northwind.post(request).then(function (data) {
                    $scope.listOfOrders = data.Customer.orders;
                });
            };
        }]);

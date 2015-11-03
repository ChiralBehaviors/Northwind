/**
 * Created by hhildebrand on 8/24/15.
 */
var northwindApp = angular.module('northwindApp', [ 'ngRoute', 'myApp' ]);

northwindApp.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/customer', {
        templateUrl : 'partials/CustomerMasterDetail.html',
        controller : 'CustomerMasterDetailCtrl'
    }).otherwise({
        redirectTo : '/customer'
    });
} ]);
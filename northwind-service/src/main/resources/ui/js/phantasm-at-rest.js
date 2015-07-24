(function() {

	var phantasm = angular.module('phantasm-at-rest', [ 'restangular' ]);

	phantasm.config([ 'RestangularProvider', function(RestangularProvider) {
		RestangularProvider.setBaseUrl("/json-ld");
	} ]);

	phantasm.factory("Ruleform", [ "Restangular", function(Restangular) {
		var service = Restangular.service("ruleform");
		return service;
	} ]);

	phantasm.factory("Facet", [ "Restangular", function(Restangular) {
		var service = Restangular.service("facet");
		return service;
	} ]);

	phantasm.factory("Workspace", [ "Restangular", function(Restangular) {
		var service = Restangular.service("workspace");
		return service;
	} ]);

	phantasm.factory("WorkspaceMediated", [ "Restangular",
			function(Restangular) {
				var service = Restangular.service("workspace-mediated");
				return service;
			} ]);

	phantasm.factory("WorkspaceLookup", [ "Restangular", function(Restangular) {
		var service = Restangular.service("workspace/lookup");
		return service;
	} ]);

})();
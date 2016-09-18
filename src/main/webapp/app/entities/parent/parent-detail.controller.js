(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('ParentDetailController', ParentDetailController);

    ParentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Parent', 'Student'];

    function ParentDetailController($scope, $rootScope, $stateParams, entity, Parent, Student) {
        var vm = this;
        vm.parent = entity;
        
        var unsubscribe = $rootScope.$on('ramHacksApp:parentUpdate', function(event, result) {
            vm.parent = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

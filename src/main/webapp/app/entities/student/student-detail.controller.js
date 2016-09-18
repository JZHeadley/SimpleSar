(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('StudentDetailController', StudentDetailController);

    StudentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Student', 'Parent'];

    function StudentDetailController($scope, $rootScope, $stateParams, entity, Student, Parent) {
        var vm = this;
        vm.student = entity;
        
        var unsubscribe = $rootScope.$on('ramHacksApp:studentUpdate', function(event, result) {
            vm.student = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();

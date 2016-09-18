(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('ParentDeleteController',ParentDeleteController);

    ParentDeleteController.$inject = ['$uibModalInstance', 'entity', 'Parent'];

    function ParentDeleteController($uibModalInstance, entity, Parent) {
        var vm = this;
        vm.parent = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Parent.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();

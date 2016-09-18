(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('ParentController', ParentController);

    ParentController.$inject = ['$scope', '$state', 'Parent', 'ParentSearch'];

    function ParentController ($scope, $state, Parent, ParentSearch) {
        var vm = this;
        vm.parents = [];
        vm.loadAll = function() {
            Parent.query(function(result) {
                vm.parents = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ParentSearch.query({query: vm.searchQuery}, function(result) {
                vm.parents = result;
            });
        };
        vm.loadAll();
        
    }
})();

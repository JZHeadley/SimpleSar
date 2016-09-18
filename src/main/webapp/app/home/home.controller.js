(function () {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'Student', 'FinancialData'];

})();
function HomeController($scope, Principal, LoginService, $state, Student, FinancialData) {
    var vm = this;

    vm.account = null;
    vm.isAuthenticated = null;
    vm.login = LoginService.open;
    vm.register = register;

    $scope.$on('authenticationSuccess', function () {
        getAccount();
    });

    getAccount();

    function getAccount() {
        Principal.identity().then(function (account) {
            vm.account = account;
            vm.isAuthenticated = Principal.isAuthenticated;
        });
    }

    function register() {
        $state.go('register');
    }

    vm.students = [];

    vm.loadAll = function () {
        Student.query(function (result) {
            vm.students = result;
        });
    };

    vm.financialData = [];
    vm.loadAllfinancialData = function () {
        FinancialData.query(function (result) {
            vm.financialData = result;
        });
    };
    vm.loadAllfinancialData();
    vm.loadAll();
}

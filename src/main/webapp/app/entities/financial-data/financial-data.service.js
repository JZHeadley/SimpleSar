(function() {
    'use strict';
    angular
        .module('ramHacksApp')
        .factory('FinancialData', FinancialData);

    FinancialData.$inject = ['$resource'];

    function FinancialData ($resource) {
        var resourceUrl =  'api/financial-data/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .factory('FinancialDataSearch', FinancialDataSearch);

    FinancialDataSearch.$inject = ['$resource'];

    function FinancialDataSearch($resource) {
        var resourceUrl =  'api/_search/financial-data/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

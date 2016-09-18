(function() {
    'use strict';
    angular
        .module('ramHacksApp')
        .factory('Student', Student);

    Student.$inject = ['$resource', 'DateUtils'];

    function Student ($resource, DateUtils) {
        var resourceUrl =  'api/students/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.dob = DateUtils.convertLocalDateFromServer(data.dob);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.dob = DateUtils.convertLocalDateToServer(data.dob);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.dob = DateUtils.convertLocalDateToServer(data.dob);
                    return angular.toJson(data);
                }
            }
        });
    }
})();

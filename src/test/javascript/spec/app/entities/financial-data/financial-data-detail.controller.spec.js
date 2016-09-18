'use strict';

describe('Controller Tests', function() {

    describe('FinancialData Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockFinancialData, MockStudent, MockParent;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockFinancialData = jasmine.createSpy('MockFinancialData');
            MockStudent = jasmine.createSpy('MockStudent');
            MockParent = jasmine.createSpy('MockParent');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'FinancialData': MockFinancialData,
                'Student': MockStudent,
                'Parent': MockParent
            };
            createController = function() {
                $injector.get('$controller')("FinancialDataDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ramHacksApp:financialDataUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

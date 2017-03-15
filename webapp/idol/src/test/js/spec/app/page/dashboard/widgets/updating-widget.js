/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

define([
    'underscore',
    'jquery',
    'find/idol/app/page/dashboard/widgets/updating-widget',
    'find/idol/app/page/dashboard/update-tracker-model'
], function(_, $, UpdatingWidget, UpdateTrackerModel) {
    'use strict';

    const spies = jasmine.createSpyObj('spies', ['onComplete', 'onIncrement', 'onCancelled', 'doUpdate']);

    const TestUpdatingWidget = UpdatingWidget.extend(spies);

    describe('Updating Widget', function() {
        beforeEach(function() {
            this.widget = new TestUpdatingWidget({
                name: 'Test Widget'
            });

            this.widget.render();

            this.updateDeferred = $.Deferred();

            this.updateTrackerModel = new UpdateTrackerModel();
        });

        afterEach(function() {
            _.each(spies, function(spy) {
                spy.calls.reset();
            })
        });

        describe('when the update is synchronous', function() {
            beforeEach(function() {
                this.widget.doUpdate.and.callFake(function(done) {
                    done();
                });

                this.widget.update(this.updateTrackerModel);
            });

            it('it should increment the model when the done callback is called', function() {
                expect(this.updateTrackerModel.get('count')).toBe(1);
            });

            it('should call onIncrement when the count increases', function() {
                // count was increases when the widget updated
                expect(this.widget.onIncrement.calls.count()).toBe(1);
            });

            it('should call onComplete when the model is set to complete', function() {
                this.updateTrackerModel.set('complete', true);

                expect(this.widget.onComplete.calls.count()).toBe(1);
            });

            it('should call onCancelled when the model is set to cancelled', function() {
                this.updateTrackerModel.set('cancelled', true);

                expect(this.widget.onCancelled.calls.count()).toBe(1);
            });
        });

        describe('when the update is asynchronous', function() {
            beforeEach(function() {
                // when a test resolves the deferred, call the done callback
                this.widget.doUpdate.and.callFake(function(done) {
                    this.updateDeferred.done(done);
                }.bind(this));

            });

            it('should not show the loading spinner until the update begins', function() {
                expect(this.widget.$('.widget-loading-spinner')).toHaveClass('hide');
            });

            describe('and the model updates', function() {
                beforeEach(function() {
                    this.widget.update(this.updateTrackerModel);
                });

                it('should show the loading spinner until the update completes', function() {
                    expect(this.widget.$('.widget-loading-spinner')).not.toHaveClass('hide');

                    this.updateDeferred.resolve();

                    expect(this.widget.$('.widget-loading-spinner')).toHaveClass('hide');
                });

                it('should not increment the model until the update is complete', function() {
                    expect(this.updateTrackerModel.get('count')).toBe(0);

                    this.updateDeferred.resolve();

                    expect(this.updateTrackerModel.get('count')).toBe(1);
                });

                it('should call onIncrement when the count increases', function() {
                    this.updateTrackerModel.increment();

                    expect(this.widget.onIncrement.calls.count()).toBe(1);
                });

                it('should call onComplete when the model is set to complete', function() {
                    this.updateTrackerModel.set('complete', true);

                    expect(this.widget.onComplete.calls.count()).toBe(1);
                });

                it('should call onCancelled when the model is set to cancelled', function() {
                    this.updateTrackerModel.set('cancelled', true);

                    expect(this.widget.onCancelled.calls.count()).toBe(1);
                });
            });
        })
    });
});

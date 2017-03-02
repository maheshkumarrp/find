define([
    'backbone',
    'jquery',
    'text!find/templates/app/page/search/filters/parametric/parametric-value-view.html'
], function(Backbone, $, template) {
    'use strict';

    return Backbone.View.extend({
        className: 'parametric-value-element selectable-table-item clickable',
        tagName: 'tr',

        initialize: function(options) {
            this.$el.attr('data-value', this.model.id);
            this.showGraphButtons = options.showGraphButtons;
        },

        render: function() {
            this.$el.html(template);

            if (!this.showGraphButtons) {
                this.$('.parametric-value-graph-cell').addClass('hide');
            }

            this.$text = this.$('.parametric-value-text');
            this.$name = this.$('.parametric-value-name');
            this.$count = this.$('.parametric-value-count');
            this.$check = this.$('.parametric-value-icon');

            this.updateText();
            this.updateCount();
            this.updateSelected();
        },

        updateText: function() {
            this.$text.tooltip('destroy');

            var name = this.model.get('displayName') || this.model.id;
            this.$name.text(name);

            this.$text.tooltip({
                placement: 'top',
                title: name,
                container: 'body'
            });
        },

        updateCount: function() {
            if(this.$count) {
                var count = this.model.get('count');

                if(count !== null) {
                    this.$count.text(' (' + count + ')');
                } else {
                    this.$count.text('');
                }
            }
        },

        updateSelected: function() {
            if(this.$check) {
                this.$check.toggleClass('hide', !this.model.get('selected'));
            }
        },

        remove: function() {
            this.$text.tooltip('destroy');

            Backbone.View.prototype.remove.apply(this, arguments);
        }
    });
});

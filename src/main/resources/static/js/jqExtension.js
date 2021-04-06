(function ($) {
  'use strict';
	$.fn.jqExtension = function(options) {
		this.change(function() {
      if (this.type === 'file') {
        var getExt = this.value.split('.').pop().toLowerCase(),
            defaults = {
              allowed: 'txt,TXT',
              invalidMessage: 'Archivo inv\u00E1lido o extensi\u00F3n incorrecta',
            },
            option = $.extend(defaults, options),
            extArray = option.allowed.split(','),
            status = $(this).next('.file-message');

        if ($.isArray(extArray) && $.inArray(getExt, extArray) > -1) {
          //status.text(option.validMessage).css('color', 'green');
        } else {
          status.text(option.invalidMessage).css('color', 'red');

        }
        return;
      }
    });
  }
})(jQuery);
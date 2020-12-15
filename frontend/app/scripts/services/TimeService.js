'use strict';
define(['frontend', 'js-joda'], function (frontend, JSJoda) {

  var LocalDateTime = JSJoda.LocalDateTime;
  var ChronoUnit = JSJoda.ChronoUnit;

  var spanishMap = {
    'years': {
      1: 'Hace un año',
      other: 'Hace {} años'
    },
    'months': {
      1: 'Hace un mes',
      other: 'Hace {} meses'
    },
    'weeks': {
      1: 'Hace una semana',
      other: 'Hace {} semanas'
    },
    'days': {
      1: 'Hace un día',
      other: 'Hace {} días'
    },
    'hours': {
      1: 'Hace una hora',
      other: 'Hace {} horas'
    },
    'minutes': {
      1: 'Hace 1 minuto',
      other: 'Hace {} minutos'
    }
  }
  var otherMap = {
    'years': {
      1: 'A year ago',
      other: '{} years ago',
    },
    'months': {
      1: 'A month ago',
      other: '{} months ago'
    },
    'weeks': {
      1: 'A week ago',
      other: '{} weeks ago'
    },
    'days': {
      1: 'A day ago',
      other: '{} days ago'
    },
    'hours': {
      1: 'An hour ago',
      other: '{} hours ago'
    },
    'minutes': {
      1: 'A minute ago',
      other: '{} minutes ago'
    }
  }

  frontend.service('TimeService', function () {
    return {

      getYearsSinceCreation: function (creationDate) {
        var dt1 = LocalDateTime.parse(creationDate);
        var dt2 = LocalDateTime.now();

        return dt1.until(dt2, ChronoUnit.YEARS);
      },

      getMonthsSinceCreation: function (creationDate) {
        var dt1 = LocalDateTime.parse(creationDate);
        var dt2 = LocalDateTime.now();

        return dt1.until(dt2, ChronoUnit.MONTHS);
      },

      getWeeksSinceCreation: function (creationDate) {
        var dt1 = LocalDateTime.parse(creationDate);
        var dt2 = LocalDateTime.now();

        return dt1.until(dt2, ChronoUnit.WEEKS);
      },

      getDaysSinceCreation: function (creationDate) {
        var dt1 = LocalDateTime.parse(creationDate);
        var dt2 = LocalDateTime.now();

        return dt1.until(dt2, ChronoUnit.DAYS);
        },

      getHoursSinceCreation: function (creationDate) {
        var dt1 = LocalDateTime.parse(creationDate);
        var dt2 = LocalDateTime.now();

        return dt1.until(dt2, ChronoUnit.HOURS);
      },

      getMinutesSinceCreation: function (creationDate) {
        var dt1 = LocalDateTime.parse(creationDate);
        var dt2 = LocalDateTime.now();

        return dt1.until(dt2, ChronoUnit.MINUTES);
      },

      getTimeForm: function(creationDate, id){
        if(this.getYearsSinceCreation(creationDate) > 0) {
          return id === 'es'? spanishMap['years'] : otherMap['years'];
        }
        else if (this.getMonthsSinceCreation(creationDate) > 0) {
          return id === 'es'? spanishMap['months'] : otherMap['months'];
        }
        else if (this.getDaysSinceCreation(creationDate) > 0) {
          return id === 'es'? spanishMap['days'] : otherMap['days'];
        }
        else if (this.getWeeksSinceCreation(creationDate) > 0) {
          return id === 'es'? spanishMap['weeks'] : otherMap['weeks'];
        }
        else if (this.getHoursSinceCreation(creationDate) > 0) {
          return id === 'es'? spanishMap['hours'] : otherMap['hours'];
        }
        else
          return id === 'es'? spanishMap['minutes'] : otherMap['minutes'];
      },

      getTimeVar: function (creationDate) {
        var time;
        if((time = this.getYearsSinceCreation(creationDate)) > 0) {
          return time;
        }
        else if ((time = this.getMonthsSinceCreation(creationDate)) > 0) {
          return time;
        }
        else if ((time = this.getDaysSinceCreation(creationDate)) > 0) {
          return time;
        }
        else if ((time = this.getWeeksSinceCreation(creationDate)) > 0) {
          return time;
        }
        else if ((time = this.getHoursSinceCreation(creationDate)) > 0) {
          return time;
        }
        else
          return this.getMinutesSinceCreation(creationDate);
      },

      getYearsMessage: function (id) {
        if(id === 'es'){
          return {
            1: 'Hace 1 año',
            other: 'Hace {} años'
          }
        }
        return {
          1: 'A year ago',
          other: '{} years ago'
        }
      },
      getMonthsMessage: function (id) {
        if(id === 'es'){
          return {
            1: 'Hace 1 mes',
            other: 'Hace {} meses'
          }
        }
        return {
          1: 'A month ago',
          other: '{} months ago'
        }
      },
      getWeeksMessage: function (id) {
        if(id === 'es'){
          return {
            1: 'Hace 1 semana',
            other: 'Hace {} semanas'
          }
        }
        return {
          1: 'A week ago',
          other: '{} weeks ago'
        }
      },
      getDaysMessage: function (id) {
        if(id === 'es'){
          return {
            1: 'Hace 1 día',
            other: 'Hace {} días'
          }
        }
        return {
          1: 'A day ago',
          other: '{} days ago'
        }
      },

      getMinutesMessage: function (id) {
        if(id === 'es'){
          return {
            1: 'Hace 1 minuto',
            other: 'Hace {} minutos'
          }
        }
        return {
          1: 'A minute ago',
          other: '{} minutes ago'
        }
      },
    }
  });
});


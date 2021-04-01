
export default {
    install (Vue, options) {
        Vue.prototype.columnFilters = function (tableDatam,columns) {
            var set = new Set();
            tableDatam.forEach(d =>{
                var _d = d;
                var has = false;
                for (let i = 0; i < columns.length; i++) {
                if (_d != undefined && _d !== null) {
                    has = true
                    _d = _d[columns[i]];
                }
                }
                if (has)   set.add(_d)
            })
            return Array.from(set).map(v => {
                return {text:v,'value': v}
            });
        };
        Vue.prototype.filterHandler = function (value, row, column) {
            const property = column['property'];
            if (property === undefined) return false;
            const propertys = property.split('.');
            var d = row;
            var has = false;
            for (let i = 0; i < propertys.length; i++) {
                if (d !== undefined && d !== null) {
                    d = d[propertys[i]];
                    has = true;
                }
            }
            return has && d === value;
        };

    }
  }
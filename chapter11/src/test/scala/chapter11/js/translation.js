// 代码清单 11-15
// #snip
describe('Translator', function() {
  describe('#translate()', function() {
    it('should yield the correct result', function() {
      return tr.translate('Hur mår du?')
               .should.eventually.equal('How are you?');
    })
  })
});
// #snip

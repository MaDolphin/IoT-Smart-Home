import { TitleTranslationPipe } from '@shared/pipes/title-translation.pipe';


describe('TranslationPipe', () => {

  const translations = [
    {source: 'overview', target: 'Übersicht'},
    {source: 'fakultaet', target: 'Fakultät'},
    {source: 'aendern', target: 'Ändern'},
    {source: 'begruendung', target: 'Begründung'}
  ];

  it('should translate the titles properly', () => {
    const pipe = new TitleTranslationPipe();
    for (let translation of translations) {
      expect(pipe.transform(translation.source)).toEqual(translation.target);
    }
  });
});

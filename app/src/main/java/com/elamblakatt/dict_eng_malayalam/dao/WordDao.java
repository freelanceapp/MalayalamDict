package com.elamblakatt.dict_eng_malayalam.dao;


import com.elamblakatt.dict_eng_malayalam.model.Word;

import java.util.List;


public interface WordDao {
    Word getWordById(int id);
    Word getWordByEnglishWordName(String englishName);
    String getProunceFromWordName(String word);
    String getMeaningFromEnglishWord(int id);
    String getMeaningFromLangWord(int id);
    List<Word> getWordByEnglishWordSrNo(int englishName);
    List<Word> getWordListByPrefixMatching(String prefix);
    List<Word> getWordListByPrefixMatching_Language(String prefix);
    List<Word> getFavoriteWordList();
    List<Word> getSearchHistory();
    List<String> getEngilshWordByPrefixMatching(String prefix);
    Word getWordFromId(int ID);
    //void addToSearchHistory(Word word);
    void deleteAllHistory();
    void makeFavorite(int id);
    void addToSearchHistory(Word id);
    void removeFromFavorite(int id);
    void removeWordFromHistory(Word word);
    void insertWord(Word word);
    void updateWord(Word word);
    void deleteWord(int id);

}

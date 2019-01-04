package io.github.shwuup.freq
import com.atilika.kuromoji.ipadic.Tokenizer

import collection.JavaConverters._
import scala.collection.mutable
import scala.io.Source

object FreqGetter {
  val jlptDic: mutable.Map[String, String] = {
    println("Currently loading CSV")
    val jlptDic = scala.collection.mutable.Map[String, String]()
    val csv = getClass.getResource("/jlptvocab.csv")
    val bufferedSource = Source.fromURL(csv)
    for (line <- bufferedSource.getLines) {
      val k = line.split(",")
      jlptDic += (k(1) -> k(0))
    }
    jlptDic
  }

  def apply(japText: String) = {
    val tokenizer = new Tokenizer()
    val tokens = tokenizer.tokenize(japText).asScala
    var wordsInText = scala.collection.mutable.HashMap.empty[String, JWord]
    for (tok <- tokens) {
      val word = tok.getBaseForm
      val level = jlptDic.get(word)
      level match {
        case Some(value) =>
          if (wordsInText.contains(word)) {
            val oldJWord = wordsInText.getOrElse(word, throw new Exception("hey this shouldn't happen"))
            val newJWord = oldJWord.copy(freq = oldJWord.freq + 1)
            wordsInText += (word -> newJWord)
          }
          else wordsInText += (word -> JWord(word,value, 1))
        case None => println(s"$word not found")
      }
    }
    val wordsInTextList = wordsInText.toList.map(x => x._2).sorted
    val totalWords = wordsInTextList.length
    (totalWords, wordsInTextList)
  }
}

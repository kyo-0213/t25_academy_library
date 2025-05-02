package jp.co.metateam.library.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.commons.lang3.StringUtils;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;


/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController {
    
    private final BookMstService bookMstService;

    @Autowired
    public BookController(BookMstService bookMstService){
        this.bookMstService = bookMstService;
    }

    @GetMapping("/book/index")
    public String index(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();
            // List<String> allTitles = bookMstRepository.findAllTitles();
            // List<String> allIsbns = bookMstRepository.findAllIsbns();
    
            // model.addAttribute("AllTitles", allTitles);
            // model.addAttribute("AllIsbns", allIsbns);
        
        model.addAttribute("bookMstList", bookMstList);

        return "book/index";
    }

    @GetMapping("/book/add")
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }
        return "book/add";
    }

    @PostMapping("/book/add")
    public String add(@Valid @ModelAttribute BookMstDto bookMstDto, BindingResult result, RedirectAttributes ra, Model model){
        boolean errTitleFlg = false;
        boolean errIsbnFlg = false;
        //BookMst titlelExist = this.bookMstService.selectByTitle(bookMstDto.getTitle());
        // BookMst isbnExist = this.bookMstService.selectByIsbn(bookMstDto.getIsbn());
        String title = bookMstDto.getTitle();
        String isbn = bookMstDto.getIsbn();
        
        String errorMsgTitle = "";
        List <String> errorIsbnList = new ArrayList<String>();
        
        if (StringUtils.isEmpty(title)) {
            errorMsgTitle= "書籍名は必須です";
            //model.addAttribute("errorMsgTitle",errorMsgTitle);
            errTitleFlg = true;
        }
        if (title.length() > 2) {
            errorMsgTitle= "書籍名は255文字以下で入力してください";
            //model.addAttribute("errorMsgTitle",errorMsgTitle);
            errTitleFlg = true;
        }
        if (StringUtils.isEmpty(isbn)) {
            errorIsbnList.add("ISBNは必須です");
            result.rejectValue("isbn", "error.value", "ISBNは必須です");
            errIsbnFlg = true;
        }else{
            if (isbn.length() != 13) {
            errorIsbnList.add("ISBNは13文字で入力してください");
            result.rejectValue("isbn", "error.value", "ISBNは13文字で入力してください");
            errIsbnFlg = true;
            }
            if (!isbn.matches("^[0-9]+$")) {
            errorIsbnList.add("ISBNは半角数字で入力してください");
            result.rejectValue("isbn", "error.value", "ISBNは半角数字で入力してください");
            errIsbnFlg = true;
            }
        }
        if (StringUtils.isNotEmpty(isbn)) {
            BookMst existingBook = this.bookMstService.selectByIsbn(isbn);
            if (existingBook != null) {
                errorIsbnList.add("登録済みのISBNです");
                errIsbnFlg = true;
            }
        }
        if (errTitleFlg || errIsbnFlg || result.hasErrors()) {
            model.addAttribute("errorMsgTitle", errorMsgTitle);
            model.addAttribute("errorIsbnList", errorIsbnList);
            model.addAttribute("bookMstDto", bookMstDto);
            return "book/add";
        }
            this.bookMstService.save(bookMstDto);
        return "redirect:/book/index";
        }
    }
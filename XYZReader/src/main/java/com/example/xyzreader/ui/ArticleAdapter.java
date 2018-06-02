package com.example.xyzreader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.squareup.picasso.Picasso;

public class ArticleAdapter extends CursorAdapter<ArticleAdapter.ViewHolder> {
    private Context context;


    public ArticleAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parent.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));

                ((Activity) context).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {

        holder.loadArticle(cursor);

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailView;
        private TextView titleView;
        private TextView subtitleView;
        private LinearLayout articleLinearLayout;
        final int defaultColor;

        private ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
            articleLinearLayout = (LinearLayout) view.findViewById(R.id.ll_article_item_details);
            defaultColor = view.getResources().getColor(R.color.theme_primary_dark);
        }

        private void loadArticle(Cursor cursor) {
            titleView.setText(cursor.getString(ArticleLoader.Query.TITLE));
            subtitleView.setText(
                    DateUtils.getRelativeTimeSpanString(
                            cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString());
            ViewGroup.LayoutParams layoutParams = thumbnailView.getLayoutParams();
            int ratioTrunked = (int) cursor.getFloat(ArticleLoader.Query.ASPECT_RATIO);
            layoutParams.height = layoutParams.width * ratioTrunked;
            thumbnailView.setLayoutParams(layoutParams);
            Picasso.with(context).load(cursor.getString(ArticleLoader.Query.THUMB_URL)).into(thumbnailView);
            if (thumbnailView != null && thumbnailView.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) thumbnailView.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        int darkMutedColor = palette.getDarkMutedColor(defaultColor);

                        if (articleLinearLayout != null) {
                            articleLinearLayout.setBackgroundColor(darkMutedColor);
                        }
                    }
                });
            }
        }
    }


}
